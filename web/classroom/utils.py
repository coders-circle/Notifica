from classroom.models import *
import json


def isValidUser(user):
    return user and user.is_authenticated and user.is_active


def getTeacher(user):
    try:
        return Teacher.objects.get(user=user)
    except:
        return None


def getStudent(user):
    try:
        return Student.objects.get(user=user)
    except:
        return None
