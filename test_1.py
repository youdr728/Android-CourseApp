import unittest
from flask import json
from app import app, db, Course, User, bcrypt

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

    '''     
    def test_feature_one(self):
        # Your test code here
        self.assertEqual('foo'.upper(), 'FOO')
    '''

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
            self.assertEqual(user_count, 2)
            user_in_db = User.query.filter_by(username=user).first()
            self.assertEqual(user_in_db.username, user)


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
        with self.app.app_context():
            user = User.query.filter_by(username='testuser').first()
            print(user.username)
            self.assertIsNotNone(user)
            print("hashed password: ", user.password)
            self.assertTrue(bcrypt.check_password_hash(user.password, 'testpass'))

        login_data = {'username': 'testuser', 'password': user.password}
        response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        self.assertEqual(response.status_code, 200)
        self.assertIn('token', json.loads(response.data))

    def test_login_failure(self):
        login_data = {'username': 'testuser', 'password': 'wrongpassword'}
        response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
        self.assertEqual(response.status_code, 400)
        self.assertIn('User Name or Password is Incorrect', json.loads(response.data)['message'])

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

    def test_logout_success(self):
        with self.app.app_context():
            user = User.query.filter_by(username='testuser').first()
            self.assertIsNotNone(user)

        if bcrypt.check_password_hash(user.password, 'testpass'):
            print("inside the ")
            login_data = {'username': 'testuser', 'password': user.password}
            login_response = self.client.post('/user/login', data=json.dumps(login_data), content_type='application/json')
            token = json.loads(login_response.data)['token']
            header = {'Authorization': "Bearer " + token}

        logout_response = self.client.post('/user/logout', headers=header)
        self.assertEqual(logout_response.status_code, 200)
        self.assertIn('you have been logged out', json.loads(logout_response.data)['message'])


if __name__ == '__main__':
    unittest.main()
