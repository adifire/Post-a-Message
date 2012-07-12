#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2
import json
import jinja2
import os
import logging
import time

from google.appengine.ext import db

jinja_environment = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))

def convert_date(value):
	ms = time.mktime(value.utctimetuple())
	ms += getattr(value, 'microseconds', 0) / 1000
	return int(ms)

class User(db.Model):
	# User model
	# username
	username = db.StringProperty(required=True)
	
	def toDict(self):
		user = {}
		user["username"] = self.username
		return user

class Message(db.Model):
	# Message Model
	
	# Message Content
	content = db.TextProperty(required=True)
	
	# Date Created
	created = db.DateTimeProperty(auto_now_add=True)
	
	# Owner
	owner = db.ReferenceProperty(User,collection_name='messages')
	
	def toDict(self):
		message = {}
		message["content"] = self.content
		message["date_created"] = convert_date(self.created)
		message["owner"] = self.owner.username
		return message


class UserHandler(webapp2.RequestHandler):
	# Handle User related requests
	def post(self, id):
		#logging.info(self.request.get('username'))
		data = self.request.get('username')#json.loads(self.request.body)
		user = User.all().filter('username',data).get()
		if user == None:
			user = User(username=data)
			user.put()
		return self.response.out.write(json.dumps(user.toDict()))
		

class MessageHandler(webapp2.RequestHandler):
	# Handle Message related requests
	def post(self, id):
		# Check if it's request json serializable, if not load them separately
		try:
			data = json.loads(self.request.body)
		except ValueError:
			data = {}
			request = self.request
			data['username'] = request.get('username')
			data['content'] = request.get('content')
		except:
			return self.error('403')
		
		# Check if the user exists. If not, then show error
		user = User.all().filter('username',data['username']).get()
		logging.info(data['username'])
		if user:
			message = Message(content=data['content'],owner=user)
			message.put()
			return self.response.out.write(json.dumps(message.toDict()))
		return self.error('403')
	
	def get(self,id):
		# Check if it's for specific message or to get all messages
		if id != '':
			message = Message.get_by_id(int(id))
			if message:
				return self.response.out.write(json.dumps(message.toDict()))
			return self.error(403)
		# Check if messages of particular or all messages
		try:
			data = json.loads(self.request.body)
			messages = User.all().filter('username',data['username']).get().messages
		except:
			messages = Message.all()
		messages_to_send = []
		for message in messages:
			messages_to_send.append(message.toDict())
		return self.response.out.write(json.dumps(messages_to_send))
	
	def put(self,id):
		# To modify particular message
		if id != '':
			data = json.loads(self.request.body)
			message = Message.get_by_id(int(id))
			if message:
				message.content = data['content']
				message.put()
				return self.response.out.write(json.dumps(message.toDict()))
		return self.error(403)
	
	def delete(self,id):
		# Delete a particular message
		if id != '':
			message = Message.get_by_id(int(id))
			message.delete()


class TestHandler(webapp2.RequestHandler):
	# Test handler that loads test page with 
	# forms for creating user and posting message
	
	def get(self):
		messages = []
		q = Message.all()
		for m in q:
			messages.append(m.toDict())
		template = jinja_environment.get_template('test.html')
		self.response.out.write(template.render({'messages':messages}))

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.out.write('Hello world!')

app = webapp2.WSGIApplication([('/', MainHandler),
								('/test', TestHandler),
								('/user\/?([0-9]*)', UserHandler),
								('/messages\/?([0-9]*)', MessageHandler)],
                              debug=True)
