from datetime import *


from flask import Flask
from flask import jsonify
from flask import request
from flask_sqlalchemy import SQLAlchemy
import os
from flask_bcrypt import Bcrypt
from flask_jwt_extended import *


app = Flask(__name__)

ACCESS_EXPIRES = timedelta(hours=2)

if "AZURE_POSTGRESQL_CONNECTIONSTRING" in os.environ:
    conn = os.environ["AZURE_POSTGRESQL_CONNECTIONSTRING"]
    values = dict(x.split("=") for x in conn.split(' '))
    user = values['user']
    host = values['host']
    database = values['dbname']
    password = values['password']
    db_uri = f'postgresql+psycopg2://{user}:{password}@{host}/{database}'
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
    debug_flag = False
    '''
if 'WEBSITE_HOSTNAME' in os.environ:  # running on Azure: use postgresql
    database = os.environ['DBNAME']  # postgres
    host_root = '.postgres.database.azure.com'
    host = os.environ['DBHOST']  # app-name + root
    user = os.environ['DBUSER']
    password = os.environ['DBPASS']
    dbname = course - app - zaish - youdr - database
    host = course - app - zaish - youdr - server.postgres.database.azure.com
    port = 5432
    sslmode = require
    user = yazegzuyeh
    password = 0
    AHF31077L6S65XM$
    db_uri = "postgres://courseapp_3spj_user:zwl2moyGOJBN1X7P5gGUEzb9UewhLfMi@dpg-clf4ha415k1s73f8e780-a/courseapp_3spj" #f'postgresql+psycopg2://{user}:{password}@{host}/{database}'
    debug_flag = False
    '''
    '''
    else: 
    # when running locally: use sqlite
db_path = os.path.join(os.path.dirname(__file__), 'our.db')
db_uri = 'sqlite:///{}'.format(db_path)
debug_flag = True
    '''
app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
app.config['JWT_SECRET_KEY'] = "This is a very very secret key that I hide from you"
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = ACCESS_EXPIRES


db = SQLAlchemy(app)
messages = []
bcrypt = Bcrypt(app)
jwt = JWTManager(app)


followers = db.Table('followers',
    db.Column('follower_id', db.Integer, db.ForeignKey('user.id')),
    db.Column('followed_id', db.Integer, db.ForeignKey('user.id'))
)


class Course(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    course_name = db.Column(db.String(128), index=True, unique=True)
    course_info = db.Column(db.String(300), index=True, unique=True)
    comments = db.relationship('Comment', backref='course', lazy='dynamic')
    users_liked = db.relationship(
        'User', secondary='likes', backref=db.backref('liked_courses', lazy='dynamic'))

    def __init__(self, course_name, course_info):
        self.course_name = course_name
        self.course_info = course_info

    def to_dict(self):
        return {
            "id": self.id,
            "course_name": self.course_name,
            "course_info": self.course_info,
        }

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(64), index=True, unique=True)
    password = db.Column(db.String(200), unique=False, nullable=False)
    comments = db.relationship('Comment', backref='author', lazy='dynamic')
    followed = db.relationship(
        'User', secondary=followers,
        primaryjoin=(followers.c.follower_id == id),
        secondaryjoin=(followers.c.followed_id == id),
        backref=db.backref('followers', lazy='dynamic'), lazy='dynamic')

    def __init__(self, username, password):
        self.username = username
        self.password = bcrypt.generate_password_hash(password).decode('utf-8')
        
    def to_dict(self):
        return {
            "id": self.id,
            "username": self.username,
            "password": self.password,
        }


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.String(140), nullable=False)
    course_id = db.Column(db.Integer, db.ForeignKey('course.id'), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    username = db.Column(db.String(40), unique = False, nullable = False)

    def to_dict(self):
        return {
            "id": self.id,
            "text": self.text,
            "course_id": self.course_id
            ,
            "user_id": self.user_id,
            "username": self.username,
        }


likes = db.Table('likes',
    db.Column('user_id', db.Integer, db.ForeignKey('user.id')),
    db.Column('course_id', db.Integer, db.ForeignKey('course.id'))
)

class TokenBlocklist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    jti = db.Column(db.String(36), nullable=False)
    created_at = db.Column(db.DateTime, nullable=False)

@jwt.token_in_blocklist_loader
def check_if_token_revoked(jwt_header, jwt_payload):
    jti = jwt_payload['jti']
    token = db.session.query(TokenBlocklist.id).filter_by(jti=jti).scalar()
    return token is not None

@app.route("/")
def hello_world():
    return "hello world!"

@app.route("/users", methods=["GET"])
def get_users():
    users = User.query.all()
    return jsonify([{"id": user.id, "username": user.username} for user in users]), 200

@app.route("/courses", methods=["GET"])
def get_courses():
    result = []
    courses = Course.query.all()
    for course in courses:
        result.append(course.to_dict())
    return jsonify(courses=result), 200

@app.route("/comments/<int:CourseID>", methods=["GET"])
def get_comments(CourseID):
    result = []
    comments = Comment.query.all()
    for comment in comments:
        if comment.course_id == CourseID:
            result.append(comment.to_dict())
    return jsonify(courses=result), 200

@app.route("/get_username/<int:user_id>", methods=["GET"])
def get_username(user_id):
    user = User.query.filter_by(id=user_id).first()
    if user is None:
        return jsonify({"error": "User not found"}), 404
    else:
        return jsonify({"username": user.username}), 200
        
@app.route("/get_user", methods = ["GET"])
@jwt_required()
def get_user():
	user_name = get_jwt_identity()
	user = User.query.filter_by(username=user_name).first()
	return user.to_dict(), 200

@app.route("/get_followed_users", methods=["GET"])
@jwt_required()
def get_followed_users():
    username = get_jwt_identity()
    user = User.query.filter_by(username=username).first()
    if user is None:
        return jsonify({"error": "User not found"}), 404
    followed_users = [u.username for u in user.followed.all()]
    return jsonify({"followed_users": followed_users}), 200


'''
@app.route("/add_user/<username>", methods=["POST"])
def add_user(username):
    new_user = User(username=username)
    db.session.add(new_user)
    db.session.commit()
    return jsonify({"message": "User added successfully"}), 200
'''

@app.route("/register", methods=["POST"])
def register():
    data = request.json
    user = data['username']
    desired_password = data['password']
    target_user = User.query.filter_by(username=user).first()
    if target_user is not None:
        return jsonify({"message": "User " + user + " Already Exists"}), 400
    created_user = User(username=user, password=desired_password)
    db.session.add(created_user)
    db.session.commit()
    return jsonify({"message": "User " + user + " Has Been Created"}), 200

@app.route("/user/login", methods=["POST"])
def login():
    data = request.json
    user_name = data['username']
    password = data['password']
    target_user = User.query.filter_by(username=user_name).first()
    if target_user is None:
        return jsonify({"message": "User Name or Password is Incorrect"}), 400
    if bcrypt.check_password_hash(target_user.password, password):
        return jsonify({'token': create_access_token(identity=target_user.username)}), 200
    return jsonify({"message": "User Name or Password is Incorrect"}), 400


@app.route("/user/logout", methods=["POST"])
@jwt_required()
def logout():
    jti = get_jwt()["jti"]
    now = datetime.now(timezone.utc)
    db.session.add(TokenBlocklist(jti=jti, created_at=now))
    db.session.commit()
    return jsonify({"message": "you have been logget out"}), 200



@app.route("/add_courses", methods=["POST"])
def add_courses():
    courses_names = request.json.get("course_names", [])
    infoArray = ["TDDD78: OO-programmering och Java",
    "TSEA28: Datorteknik Y",
    "TDDD80: Mobila och sociala applikationer",
    "TDDD79: Ingenjörsprofessionalism, del 2"]
    i = 0
    added_courses = []

    for course_name in courses_names:
            info = infoArray[i]
            existing_course = Course.query.filter_by(course_name=course_name, course_info=info).first()
            if existing_course:
                continue

            new_course = Course(course_name=course_name, course_info=info)
            db.session.add(new_course)
            added_courses.append(course_name)
            i += 1

    db.session.commit()
    return jsonify({"message": "Courses added successfully", "added_courses": added_courses}), 200


@app.route("/like_course/<CourseName>", methods=["POST"])
@jwt_required()
def like_Course(CourseName):
    course = Course.query.filter_by(course_name=CourseName).first()
    if course is None:
        return jsonify({"message": "Error"}), 400
    username = get_jwt_identity()
    user = User.query.filter_by(username=username).first()
    if user is None:
        return jsonify({"message": "user not found"}), 400
    if user in course.users_liked:
        return jsonify({"message": "You have already liked this course"}), 400
    course.users_liked.append(user)
    db.session.commit()
    return jsonify({"message": "Successfully liked"}), 200

@app.route("/unlike_course/<CourseName>", methods=["POST"])
@jwt_required()
def unlike_Course(CourseName):
    course = Course.query.filter_by(course_name=CourseName).first()
    if course is None:
        return jsonify({"error": "Error"}), 400
    username = get_jwt_identity()
    user = User.query.filter_by(username=username).first()
    if user is None:
        return jsonify({"error": "user not found"}), 400
    if user in course.users_liked:
        course.users_liked.remove(user)
        db.session.commit()
        return jsonify({"message": "Unliked Course!"}), 200
    return jsonify({"message": "You have to like the course first to perform this action"}), 400


@app.route("/unfollow_user/<Username>", methods=["POST"])
@jwt_required()
def unfollow_User(Username):
    user_followed = User.query.filter_by(username=Username).first()
    if user_followed is None:
        return jsonify({"message": "Error"}), 400
    followername = get_jwt_identity()
    follower = User.query.filter_by(username=followername).first()
    if follower is None:
        return jsonify({"message": "user not found"}), 400
    if follower in user_followed.followers:
        user_followed.followers.remove(follower)
        db.session.commit()
        return jsonify({"message": "Successfully unfollowed"}), 200
    return jsonify({"message": "You must follow the user first"}), 400

@app.route("/follow_user/<Username>", methods=["POST"])
@jwt_required()
def follow_User(Username):
    user_followed = User.query.filter_by(username=Username).first()
    if user_followed is None:
        return jsonify({"message": "Error"}), 400
    followername = get_jwt_identity()
    follower = User.query.filter_by(username=followername).first()
    if follower is None:
        return jsonify({"message": "user not found"}), 400
    if follower not in user_followed.followers:
        user_followed.followers.append(follower)
        db.session.commit()
        return jsonify({"message": "Successfully followed"}), 200
    return jsonify({"message": "user already followed"}), 400

@app.route("/show_users_comments/<Username>", methods = ["GET"])
def show_users_comments(Username):
    user = User.query.filter_by(username=Username).first()
    if not user:
        return jsonify({"error": "User not found"}), 404
    result = []
    comments = Comment.query.all()
    for comment in comments:
        if comment.user_id == user.id:
            result.append(comment.to_dict())
    return jsonify(comments=result), 200

@app.route("/show_users_likes/<Username>", methods = ["GET"])
def show_users_likes(Username):
    user = User.query.filter_by(username=Username).first()
    result = []
    courses = Course.query.all()
    for course in courses:
        if user in course.users_likes:
            result.append(course.to_dict())
    return jsonify(courses=result), 200

@app.route("/show_followed_users", methods = ["GET"])
@jwt_required()
def show_followed_users():
    user_name = get_jwt_identity()
    user = User.query.filter_by(username=user_name).first()

    if not user:
        return jsonify({"error": "User not found"}), 404

    result = []

    for users in user.followed:
        result.append(users.username)
    return jsonify(followed=result), 200

@app.route("/comment_course/<int:CourseID>", methods=["POST"])
@jwt_required()
def comment_on_course(CourseID):
    course = Course.query.filter_by(id=CourseID).first()
    user_name = get_jwt_identity()
    user = User.query.filter_by(username=user_name).first()
    print(CourseID)
    if not course:
        return jsonify({"message": "Course not found"}), 404
    data = request.json.get('text')
    comment = Comment(text=data, course_id=course.id, user_id=user.id, username=user_name)
    db.session.add(comment)
    db.session.commit()
    return jsonify(new_comment=comment.to_dict()), 200


if __name__ == "__main__":
    '''
    with app.app_context():
        db.drop_all()
        db.create_all()
    '''
    from waitress import serve
    serve(app, host="0.0.0.0", port=8000)
    app.debug = True
    #app.run(port=5000)
    #app.run(host='0.0.0.0', port=8000)
