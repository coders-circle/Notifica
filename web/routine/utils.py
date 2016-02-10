from django.db.models import Q, Max, Min

from classroom.models import *
from classroom.utils import *
from routine.models import *


def getPeriods(user):
    qset = Period.objects.none()

    # for teacher, check if period contains the teacher
    teachers = getTeachers(user)
    for teacher in teachers:
        qset = qset | Period.objects.filter(teachers=teacher)

    # for student, the class must match the routine's class
    students = getStudents(user)
    for student in students:
        qset = qset | Period.objects.filter(routine__p_class__pk=student.group.p_class.pk)

    return qset


def getElectives(user):
    qset = Elective.objects.none()

    # for teacher, check if period contains the teacher
    teachers = getTeachers(user)
    for teacher in teachers:
        qset = qset | Elective.objects.filter(teachers=teacher)

    # for student, check if period is attended by the student
    students = getStudents(user)
    for student in students:
        qset = qset | Elective.objects.filter(p_group__routine__p_class__pk=student.group.p_class.pk, students__pk=student.pk)

    return qset


def getRoutine(user):
    routine = {}
    periods = getPeriods(user)
    electives = getElectives(user)

    for d in range(7):
        routine[d] = []
    for p in periods:
        p.is_elective = False
        routine[p.day].append(p)
    for e in electives:
        p.is_elective = True
        routine[p.day].append(e)
    return routine


def getRoutineForAdmin(user):
    routine = {}
    periods = Period.objects.filter(routine__p_class__admins__pk=user.pk)
    electives = Elective.objects.filter(routine__p_class__admins__pk=user.pk)

    for d in range(7):
        routine[d] = []
    for p in periods:
        p.is_elective = False
        routine[p.day].append(p)
    for e in electives:
        p.is_elective = True
        routine[p.day].append(e)
    return routine
