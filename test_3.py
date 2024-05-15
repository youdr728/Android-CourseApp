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

    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()

    def test_logout_success(self):
        with self.app.app_context():
            user = User.query.filter_by(username='testuser').first()
            self.assertIsNotNone(user)

        login_data = {'username': 'testuser', 'password': 'testpass'}
        login_response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        token = json.loads(login_response.data)['token']
        header = {'Authorization': "Bearer " + token}

        logout_response = self.client.post('/user/logout', headers=header)
        self.assertEqual(logout_response.status_code, 200)
        self.assertIn('you have been logget out', json.loads(logout_response.data)['message'])


if __name__ == '__main__':
    unittest.main()