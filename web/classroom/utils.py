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


def get_profiles_for_user(user):
    profile = UserProfile.objects.get(user=user)
    result = [profile.profile]

    teacher = getTeacher(user)
    if teacher and teacher.department:
        result = result + [teacher.department.profile, teacher.department.organization.profile]

    student = getStudent(user)
    if student:
        cls = student.group.p_class
        result = result + [cls.profile]
        if cls.department:
            result = result + [cls.department.profile, cls.department.organization.profile]

    return [r.pk for r in result]
