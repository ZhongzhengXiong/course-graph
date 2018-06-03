# RESTful Server API Docuement
This is the document of the RESTful server.

## Note
The difference between POST and PUT is that POST is often used to create a new resource and PUT is often used to update a resource. In this project, resources are internally identified by their *id*s. Thus, we use POST method to create a new resource with a newly generated id and use PUT method to update an old resource with a known id.

All fields in **Data Params** are REQUIRED except explicitly tagged OPTIONAL.


## Resourses Path
Here we list all the resources and supported operations on them.

The plural form of the resource refer to the set of the resource. GET method on them will return a list of the resources. POST method on them will try to create a new resource.

`/_resource_/{id}` often refer to a specific resource. GET method returns the data of the resource. DELETE method deletes the resource. And PUT method will try to update the resource with new data.

- `/courses`
    - GET, all courses, support filtering by user(that the student takes or the teacher teaches)
    - POST, a new course, with specified teacher
- `/course/{cid}`
    - GET, data of the course, meta data mostly
    - DELETE, the course
    - PUT, the updated data of the course
- `/course/{cid}/graphs`
    - GET, all graphs of the course
    - POST, a new graph to the course
- `/course/{cid}/students`
    - GET, all students of the course
    - POST, a new student to the course, a.k.a. a student join the course
- `/graph/{gid}`
    - GET, data of the course, without nodes
    - DELETE, the graph
    - PUT, the updated data of the graph
- `/graph/{gid}/nodes`
    - GET, all nodes of the graph
    - POST, a new node to the graph, with specified parent node
- `/node/{nid}`
    - GET, data of the node, including parent node, children nodes, and some meta data
    - DELETE, the node
    - PUT, the updated node info
- `/node/{nid}/questions`
    - GET, all questions of a node
    - POST, a new question to the node
- `/node/{nid}/lectures`
    - GET, all lectures of a node
    - POST, a new lecture to the node
- `/node/{nid}/resources`
    - GET, all resources of a node
    - POST, a new resource to the node
- `/question/{qid}`
    - GET, data of the question
    - PUT, the updated question
    - DELETE, the question
- `/question/{qid}/answers`
    - GET, all answers of the question
    - POST, a new answer to this question
- `/answer/{aid}`
    - GET, the data of the answer
    - PUT, the updated answer, restricted for that students are not allowed to edit their answers
    - DELETE, the answer
- `/lecture/{lid}`
    - GET, the data of the lecture
    - PUT, the updated lecture
    - DELETE, the lecture
- `/resource/{rid}`
    - GET, the data of the resource
    - PUT, the updated resource
    - DELETE, the resource
- `/users`
    - GET, all users, support filtering
    - POST, a new user, registeration
- `/user/{uid}`
    - GET, data of the user
    - PUT, the updated data of the user
    - DELETE, the user
- `/token`
    - POST, a new token, used to authorization, login
    - DELETE, this token, logout



## Course Related
Meta data structure of a course:
```
course_meta :
{
    name : <string>,
    id : <integer>,
    code : <string>,
    teacher : {
        name : <string>,
        id : <integer>
    },
    created_time : <time>, # The time that the course is created
    modified_time : <time>, # Last time of the mata data of the course being modified.
    student_num : <integer>
}
```

### Create a course

Teacher create a new course. Needs authorization.

- **URL**

    /courses

- **Method:**

    `POST`

- **URL Params**

    **Required:**

    None.

    **Optional:**

    None.

- **Data Params**
    ```
    {
        name : <string>,
        code : <string>,  # 课程代码
        teacher_id : <string>
    }
    ```

    Example:
    ```
    {
        name : "Advanced Web",
        code : "GRE4T4G41N",
        teacher_id : 9528
    }
    ```

- **Success Response:**

    - **Code:** 201 <br>
      **Content:** `[course_meta]` of the newly created course
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or `teacher_id` does not match token.

- **Notes:**

    None.

----
### Delete a course

Teacher delete a course. Needs authorization.

- **URL**

    /course/{cid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `cid=<integer>`, course id

    **Optional:**

    None.

- **Data Params**
    
    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or `teacher_id` does not match token.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Course not found" }` <br>
      **Condition:** Course id invalid.

- **Notes:**

    None.

----
### List all courses

List all courses availble.

- **URL**

    /courses

- **Method:**

    `GET`

- **URL Params**

    **Required:**

    None.

    **Optional:**

    None.

- **Data Params**
    None.

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** 
    ```
    {
        courses : [
            <course_meta>,
            ...
        ],
        course_num : <integer>
    }
    ```
 
- **Error Response:**
    None.

- **Notes:**

    None.

----
### List courses of a user

List all courses that a student takes or a teacher teaches. Needs Authorization.

- **URL**

    /courses?u={uid}

- **Method:**

    `GET`

- **URL Params**

    **Required:**

    `uid=<integer>`, user id

    **Optional:**

    None.

- **Data Params**
    None.

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** 
    ```
    {
        courses : [
            <course_meta>,
            ...
        ],
        course_num : <integer>
    }
    ```
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user id does not match token.

- **Notes:**

    This could be considered as get all courses while filtered by user(student/teacher).

----
### Get data of the course

- **URL**

    /course/{cid}

- **Method:**

    `GET`

- **URL Params**

    **Required:**

    `cid=<integer>`

    **Optional:**

    None.

- **Data Params**
    None.

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** `<course_meta>`
 
- **Error Response:**

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Course not found" }` <br>
      **Condition:** Course with that id is not found.

- **Notes:**

    None.

----
### Update data of the course

Update the meta of the course. Only include the fields that need to be updated(replaced).

- **URL**

    /course/{cid}

- **Method:**

    `PUT`

- **URL Params**

    **Required:**

    `cid=<integer>`

    **Optional:**

    None.

- **Data Params**
    ```
    {
        name : <string>, # OPTIONAL
        code : <string>, # OPTIONAL
    }
    ```

    Example:
    ```
    {
        name : "More Advanced Web"
    }
    ```

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** `<course_meta>`
 
- **Error Response:**

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Course not found" }` <br>
      **Condition:** Course with that id is not found.

- **Notes:**

    None.

----
### List all students of the course

Get a list of students of a course.

- **URL**

    /course/{cid}/students

- **Method:**

    `GET`

- **URL Params**

    **Required:**

    `cid=<integer>`

    **Optional:**

    None.

- **Data Params**
    None.

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** 
    ```
    {
        students: [
            <user_meta>,
            ...
        ],
        student_num : <integer>
    }
    ```
 
- **Error Response:**

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Course not found" }` <br>
      **Condition:** Course with that id is not found.

- **Notes:**

    None.

----
### Add a new student to the course(student join the course)

Student join a course. Or add a student to a course.

- **URL**

    /course/{cid}/students

- **Method:**

    `POST`

- **URL Params**

    **Required:**

    `cid=<integer>`

    **Optional:**

    None.

- **Data Params**
    ```
    {
        uid : <integer> # Student id
    }
    ```

    Example:
    ```
    {
        uid : 9527
    }
    ```

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** `<course_meta>`, Meta data of the course that the student join.
 
- **Error Response:**

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Course not found" }` <br>
      **Condition:** Course with that id is not found.
    
    OR

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user id does not match token.

- **Notes:**

    None.

----

## Token Related

Token is used for authorization. It is stored in the memory and will be deleted if user logout or another client login with the same account. Token will also be deleted after certain amount of time.

### Login

User login. Return a newly created token if success.

- **URL**

    /token

- **Method:**

    `POST`

- **URL Params**

    **Required:**

    None.

    **Optional:**

    None.

- **Data Params**
    ```
    {
        email : <string>, # user email
        password : <string> # user password
    }
    ```

    Example:
    ```
    {
        email : "abc@fudan.edu.cn",
        password : "logmein123"
    }
    ```

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** 
    ```
    {
        user : <user_meta>,
        token : <string> # access token
    }
    ```
 
- **Error Response:**

    - **Code:** 400 BAD REUQEST <br>
      **Content:** `{ error : "Email Invalid" }` <br>
      **Condition:** Email is invalid.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "User Not Found"}` <br>
      **Condition:** Email is valid but user does not exist.
    
    OR

    - **Code:** 422 UNPROCESSABLE ENTRY <br>
      **Content:** `{ error : "Wrong Password" }` <br>
      **Condition:** User exists but password is wrong.

- **Notes:**

    Successful login will return a token. Add token to HTTP header in the following requests to authorize them.

----
### Logout

User logout. Delete a token.

- **URL**

    /token

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**
    
    None.

    **Optional:**

    None.

- **Data Params**
    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Conditon:** User not login. 

- **Notes:**

    This request will delete the token in memory thus prevent future requests from authorizing though this token.

    If the user is not in login state, which means the head of the HTTP request does not have any token, it will cause a 401 error.

----

## User Related
Meta data structure of a user:
```
user_meta :
{
    name : <string>,
    id : <integer>,
    email : <string>,
    type : <string>
}
```
### Register

Create a user. Do **NOT** log in the user after successful registeration.

- **URL**

    /users

- **Method:**

    `POST`

- **URL Params**

    **Required:**

    None.

    **Optional:**

    None.

- **Data Params**
    ```
    {
        email : <string>, # user email, unique across all users, will be checked for uniqueness
        name : <string>, # user name, used for showing on the page, allow duplication
        password : <string>, # user password
        type : <string> # user type, enum, all uppercases, "STUDENT" or "TEACHER"
    }
    ```

    Example:
    ```
    {
        email : "abc@fudan.edu.cn",
        name : "Alice B. Coffee",
        password : "logmein123",
        type : "STUDENT"
    }
    ```

- **Success Response:**

    - **Code:** 201 CREATED <br>
      **Content:** `{ id : <integer> }`, User id, used internally to identify the user
 
- **Error Response:**

    - **Code:** 400 BAD REUQEST <br>
      **Content:** `{ error : "Invalid field" }` <br>
      **Condition:** There is any invalid field.
    
    OR

    - **Code:** 409 CONFLICT <br>
      **Content:** `{ error : "User existed" }` <br>
      **Condition:** Every field is valid but user with the same email already existed.

- **Notes:**

    The syntactic validness of the fields, e.g. email, password, or name, should be checked before sent to server. Although server will do the check anyway. Invalid field or missing field will cause an error response with HTTP code 400 and an error message.

    Already registered email will result in a 409 error.

----
### Delete Acount
- **URL**

    /user/{uid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `uid=<integer>`

    **Optional:**

    None.

- **Data Params**
    
    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or `uid` does not match token.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "User not found" }` <br>
      **Condition:** User id invalid.

- **Notes:**

    None.

----
### Update user profile
### Get user profile
### List all users

## Graph Related
Meta data structure of a graph:
```
graph_meta :
{
    name : <string>,
    id : <string>,
    description : <string>,
    created_time : <time>,
    modified_time : <time>
}
```

### List all graphs of the course

- **URL**

    /course/{cid}/graphs

- **Method:**

    `GET`

- **URL Params**

    **Required:**

    `cid=<integer>` 

    **Optional:**

    None.

- **Data Params**
    None.

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** 
    ```
    {
        graphs : [
            <graph_meta>,
            ...
        ],
        graph_num : <integer>
    }
    ```
 
- **Error Response:**

    None.

- **Notes:**

    None.

----
### Add a new graph to the course

Teacher create a new graph for a course. Need authorization.

- **URL**

    /course/{cid}/graphs

- **Method:**

    `POST`

- **URL Params**

    **Required:**

    `cid=<integer>`

    **Optional:**

    None.

- **Data Params**
    ```
    {
        name : <string>, # name of the graph
        description : <string> # description of the graph
    }
    ```

    Example:
    ```
    {
        name : "Advanced Topics Graph",
        description : "This is the graph of advanced topics of the course. We will cover this part later in this course."
    }
    ```

- **Success Response:**

    - **Code:** 200 <br>
      **Content:** `<graph_meta>`, metadata of the newly created graph
 
- **Error Response:**

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Course not found" }` <br>
      **Condition:** Course id invalid.

- **Notes:**

    None.

----
### Get data of the graph
### Update data of the graph
### Delete the graph

- **URL**

    /graph/{gid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `gid=<integer>`

    **Optional:**

    None.

- **Data Params**
    
    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user is not the teacher of this course.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Graph not found" }` <br>
      **Condition:** Graph id invalid.

- **Notes:**

    None.

----

## Node Related
Meta data of a node:
```
node_meta :
{
    name : <string>,
    color : <string>,
    
}
```
### List all nodes of the graph
### Add a node to the graph
### Get data of the node
### Update date of the node
### Delete the node

- **URL**

    /node/{nid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `nid=<integer>`

    **Optional:**

    None.

- **Data Params**

    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user is not the teacher of this course.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Node not found" }` <br>
      **Condition:** Node id invalid.

- **Notes:**

    None.

----

## Resource Related
### List all resources of the node
### Add a resource to the node
### Get data of the resource
### Update data of the resource
### Delete the resource

- **URL**

    /resource/{rid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `rid=<integer>`

    **Optional:**

    None.

- **Data Params**

    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user is not the teacher of this course.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Resource not found" }` <br>
      **Condition:** Resource id invalid.

- **Notes:**

    None.

----

## Lecture Related
### List all lectures of the node
### Add a lecture to the node
### Get data of the lecture
### Update data of the lecture
### Delete the lecture

- **URL**

    /lecture/{lid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `lid=<integer>`

    **Optional:**

    None.

- **Data Params**

    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user is not the teacher of this course.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Lecture not found" }` <br>
      **Condition:** Lecture id invalid.

- **Notes:**

    None.

----

## Question Related
### List all questions of the node
### Add a question to the node
### Get data of the question
### Update data of the question
### Delete the question

- **URL**

    /question/{qid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `qid=<integer>`

    **Optional:**

    None.

- **Data Params**

    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user is not the teacher of this course.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Question not found" }` <br>
      **Condition:** Question id invalid.

- **Notes:**

    None.

----

## Answer Related
### List all answers of the question
### Add a answer to the question
### Get data of the answer
### Update the answer
### Delete the answer

- **URL**

    /answer/{aid}

- **Method:**

    `DELETE`

- **URL Params**

    **Required:**

    `aid=<integer>`

    **Optional:**

    None.

- **Data Params**

    None.

- **Success Response:**

    - **Code:** 204 NO CONTENT <br>
      **Content:** None.
 
- **Error Response:**

    - **Code:** 401 UNAUTHORIZED <br>
      **Content:** `{ error : "Unauthorized" }` <br>
      **Condition:** User not login or user is not the teacher of this course.
    
    OR

    - **Code:** 404 NOT FOUND <br>
      **Content:** `{ error : "Answer not found" }` <br>
      **Condition:** Answer id invalid.

- **Notes:**

    None.

----