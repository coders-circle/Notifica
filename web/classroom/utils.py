from classroom.models import *


def getUserType(user):
    try:
        user.teacher = Teacher.objects.get(user=user)
        return "Teacher"
    except:
        try:
            user.student = Student.objects.get(user=user)
            return "Student"
        except:
            return "Normal"


def isValidUser(user):
    return user and user.is_authenticated and user.is_active


def getLinkedObject(string):
    if not string.startswith("@"):
        return None

    strs = string.split(":")
    tp = strs[0][1:]
    nid = strs[1]

    if tp == "organization":
        return Organization.objects.get(notifica_id=nid)
    elif tp == "department":
        return Department.objects.get(notifica_id=nid)
    elif tp == "subject":
        return Subject.objects.get(notifica_id=nid)
    elif tp == "teacher":
        return Teacher.objects.get(notifica_id=nid)
    elif tp == "class":
        return Class.objects.get(notifica_id=nid)
    elif tp == "group":
        return Group.objects.get(notifica_id=nid)
    elif tp == "student":
        return Student.objects.get(notifica_id=nid)
    elif tp == "routine":
        return Routine.objects.get(notifica_id=nid)
    elif tp == "user":
        return User.objects.get(username=nid)
    return None
