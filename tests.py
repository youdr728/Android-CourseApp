import requests
import json


'''
courses = requests.get(url + "/courses")
course_id = None
for course in courses.json():
    if course["course_name"] == "TDDD78":
        course_id = course["id"]
        break
if course_id is None:
    print("course not found")
    exit(1)
'''

response2 = requests.post("http://127.0.0.1:5000" + "/add_courses",
                          json={
                              "course_names": ["TDDD78", "TSEA28", "TDDD80", "TDDD79"]
                          })

register_user_1 = requests.post("http://127.0.0.1:5000" + "/register", json={'username': 'Yousef', 'password': 'hello12'})
login_user_1 =requests.post("http://127.0.0.1:5000" + "/user/login", json={'username': 'Yousef', 'password': 'hello12'})
token1 = login_user_1.json()['token']
header1 = {'Authorization': "Bearer " + token1}
courseid = "1"
comment1 = requests.post("http://127.0.0.1:5000" + f"/comment_course/{courseid}", headers=header1, json={"text" : " not very Nice course"})
like1 = requests.post("http://127.0.0.1:5000"  + "/like_course/TDDD78", headers=header1)
like2 = requests.post("http://127.0.0.1:5000"  + "/like_course/TSEA28", headers=header1)
'''

like1 = requests.post("http://127.0.0.1:5000"  + "/like_course/TDDD78", headers=header1)
comment1 = requests.post(f"https://course-app.azurewebsites.net/comment_course/{course_id}", headers=header1, json={"text" : "Trash opinion, go kill urself"})
logout1 = requests.post("http://127.0.0.1:5000"  + "/user/logout", headers=header1)

courses = requests.get("http://127.0.0.1:5000"  + "/courses")
course_id = None
for course in courses.json():
    if course["course_name"] == "TDDD78":
        course_id = course["id"]
        break
if course_id is None:
    print("course not found")
    exit(1)

register_user_2 = requests.post("http://127.0.0.1:5000" + "/register", json={'username': 'Zain', 'password': 'bye21'})
login_user_2 =requests.post("http://127.0.0.1:5000" + "/user/login", json={'username': 'Zain', 'password': 'bye21'})
login_user_2 =requests.post("http://127.0.0.1:5000" + "/user/login", json={'username': 'Zain', 'password': 'bye21'})

token2 = login_user_2.json()['token']
header2 = {'Authorization': "Bearer " + token2}

like2 = requests.post("http://127.0.0.1:5000"  + "/like_course/TDDD78", headers=header2)
like2 = requests.post("http://127.0.0.1:5000" + "/like_course/TSEA28", headers=header2)
comment2 = requests.post("http://127.0.0.1:5000" + "/comment_course/{course_id}", headers=header2, json={"text" : "Nice course"})
comment2 = requests.post(f"http://course-app.azurewebsites.net/comment_course/{course_id}", headers=header2, json={"text" : "Very Nice course"})
follow2 = requests.post("http://127.0.0.1:5000" + "/follow_user/Yousef", headers=header2)

show_followed_users_comments2 = requests.get("http://127.0.0.1:5000" + "/show_followed_users_comments", headers=header2)

response = requests.post("http://127.0.0.1:5000/add_user/Yousef")
response1 = requests.post("http://127.0.0.1:5080/add_user/Zain")
response2 = requests.post("http://127.0.0.1:5080/like_course/TDDD78/add_courses",
                          json={
                              "course_names": ["TDDD78", "TSEA28", "TDDD80", "TDDD79"]
                          })
courses = requests.get("http://127.0.0.1:5080/courses")
course_id = None
for course in courses.json():
    if course["course_name"] == "TDDD78":
        course_id = course["course_id"]
        break

if course_id is None:
    print("Course not found.")
    exit(1)

like = requests.post(f"http://127.0.0.1:5080/like_course/TDDD78/{user_id}")
like2 = requests.post(f"http://127.0.0.1:5080/like_course/TSEA28/{user_id}")
like3 = requests.post(f"http://127.0.0.1:5080/like_course/TDDD79/{user_id}")

follow = requests.post(f"http://127.0.0.1:5080/follow_user/{user_id2}/{user_id}")
print("before request")

response3 = requests.post(f"http://127.0.0.1:5080/course/{course_id}/comment/{user_id}", json={"text": "hello there"})

if like.status_code == 200:
    print("course liked!")
else:
    print("failed!")

if follow.status_code == 200:
    print("user followed!")
else:
    print("failed!")

print(user["username"])
'''