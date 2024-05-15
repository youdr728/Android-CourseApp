import unittest
from flask import json
from app import app, db, User

class Test(unittest.TestCase):

    def setUp(self):
        self.app = app
        self.client = self.app.test_client()

        with self.app.app_context():
            db.create_all()
            user = User(username='testuser', password='testpass')
            db.session.add(user)
            db.session.commit()
            data = {'course_names': ['TDDD78', 'TSEA28']}
            self.client.post('/add_courses', data=json.dumps(data), content_type='application/json')

    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()


    def test_like_unlike_course(self):
        with self.app.app_context():
            user = User.query.filter_by(username='testuser').first()
            self.assertIsNotNone(user)

        login_data = {'username': 'testuser', 'password': 'testpass'}
        login_response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        token = json.loads(login_response.data)['token']
        header = {'Authorization': "Bearer " + token}

        like_response = self.client.post('/like_course/TDDD78', headers=header)
        self.assertEqual(like_response.status_code, 200)
        self.assertIn('Successfully liked', json.loads(like_response.data)['message'])

        unlike_response = self.client.post('/unlike_course/TDDD78', headers=header)
        self.assertEqual(unlike_response.status_code, 200)
        self.assertIn('Unliked Course!', json.loads(unlike_response.data)['message'])

    def test_unlike_course_failure(self):
        with self.app.app_context():
            user = User.query.filter_by(username='testuser').first()
            self.assertIsNotNone(user)

        login_data = {'username': 'testuser', 'password': 'testpass'}
        login_response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        token = json.loads(login_response.data)['token']
        header = {'Authorization': "Bearer " + token}

        unlike_response = self.client.post('/unlike_course/TDDD78', headers=header)
        self.assertEqual(unlike_response.status_code, 400)
        self.assertIn('You have to like the course first to perform this action', json.loads(unlike_response.data)['message'])

if __name__ == '__main__':
    unittest.main()