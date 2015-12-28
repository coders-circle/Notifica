from django.db.models import Q, Max, Min

from classroom.models import *
from classroom.utils import *
from routine.models import *

def getPeriods(user):
    qset1 = Period.objects.none()
    qset2 = Period.objects.none()

    # for teacher, check if period contains the teacher
    teacher = getTeacher(user)
    if teacher:
        qset1 = Period.objects.filter(teachers=teacher)

    # for student, the class must match the routine's class
    student = getStudent(user)
    if student:
        qset2 = Period.objects.filter(routine__p_class__pk=student.group.p_class.pk)

    return qset1 | qset2

def getRoutine(user):
    routine = {}
    periods = getPeriods(user)
    for d in range(7):
        routine[d] = []
    for p in periods:
        routine[p.day].append(p)
    return routine, periods.aggregate(Min('start_time')), periods.aggregate(Max('end_time'))
