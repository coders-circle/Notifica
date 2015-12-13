from django.db.models import Q, Max, Min

from classroom.models import *
from classroom.utils import *
from routine.models import *

def getPeriods(user):
    usertype = getUserType(user)
    periods = []

    if usertype == "Student":
        s = user.student
        periods = Period.objects.filter(Q(groups=s.group)|Q(routine__p_class=s.group.p_class)).distinct()
    elif usertype == "Teacher":
        t = user.teacher
        periods = Period.objects.filter(teachers=t).distinct()
    return periods


def getRoutine(user):
    routine = {}
    periods = getPeriods(user)
    for d in range(7):
        routine[d] = []
    for p in periods:
        routine[p.day].append(p)
    return routine, periods.aggregate(Min('start_time')), periods.aggregate(Max('end_time'))
