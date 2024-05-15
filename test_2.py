import unittest
from flask import json
from app import app, db, Course, User, bcrypt

class Test(unittest.TestCase):
    def setUp(self):
        self.app = app
        self.client = self.app.test_client()

        with self.app.app_context():
            db.create_all()
            hashed_password = bcrypt.generate_password_hash('testpass').decode('utf-8')
            user = User(username='testuser', password=hashed_password)
            db.session.add(user)
            db.session.commit()


    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()


    def test_login_success(self):
        # Test login with correct credentials
        login_data = {'username': 'testuser', 'password': 'testpass'}
        response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        self.assertEqual(response.status_code, 200)
        self.assertIn('token', json.loads(response.data))


    def test_login_failure(self):
        login_data = {'username': 'testuser', 'password': 'wrongpassword'}
        response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        self.assertEqual(response.status_code, 400)
        self.assertIn('User Name or Password is Incorrect', json.loads(response.data)['message'])


if __name__ == '__main__':
    unittest.main()