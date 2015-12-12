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
    return user and user.is_authenticated and user.is_active and not user.is_superuser
