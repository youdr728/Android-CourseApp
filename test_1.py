import unittest
import sys
import os
sys.path.append(os.path.abspath(''))
from flask import json
from app import app, db, Course, User

class Test(unittest.TestCase):
    def setUp(self):
        self.app = app
        self.client = self.app.test_client()

        with self.app.app_context():
            db.create_all()

    def tearDown(self):
        with self.app.app_context():
            db.drop_all()
            db.session.remove()

    def test_feature_add_courses(self):
        # data for request
        data = {'course_names': ['TDDD78', 'TSEA28']}

        response = self.client.post('/add_courses', data=json.dumps(data), content_type='application/json')

        # Check for the HTTP response status
        self.assertEqual(response.status_code, 200)

        # Check the response data
        response_data = json.loads(response.data)
        self.assertIn('Courses added successfully', response_data['message'])
        self.assertListEqual(response_data['added_courses'], ['TDDD78', 'TSEA28'])

        # Check if courses are added in the database
        with self.app.app_context():
            course_count = Course.query.count()
            self.assertEqual(course_count, 2)

    def test_feature_register(self):

        user = 'user'
        data = {'username': 'user' , 'password': 'userpass'}

        response = self.client.post('/register', data=json.dumps(data), content_type='application/json')

        self.assertEqual(response.status_code, 200)

        response_data = json.loads(response.data)
        self.assertIn('User ' + user + ' Has Been Created', response_data['message'])

        with self.app.app_context():
            user_count = User.query.count()
            self.assertEqual(user_count, 1)
            user_in_db = User.query.filter_by(username=user).first()
            self.assertEqual(user_in_db.username, user)


if __name__ == '__main__':
    unittest.main()
