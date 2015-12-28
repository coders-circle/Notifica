from rest_framework import viewsets
from rest_framework import permissions

from routine.models import *
from routine.utils import *
from routine.serializers import *
from classroom.permissions import *
from classroom.utils import *


class RoutineViewSet(viewsets.ModelViewSet):
    queryset = Routine.objects.all()
    serializer_class = RoutineSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)


class PeriodViewSet(viewsets.ModelViewSet):
    queryset = Period.objects.all()
    serializer_class = PeriodSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def get_queryset(self):
        return getPeriods(self.request.user)
        #qset1 = Period.objects.none()
        #qset2 = Period.objects.none()

        ## for teachers check is period contains the teacher
        #teacher = getTeacher(self.request.user)
        #if teacher:
        #    allperiods = Period.objects.all()
        #    pids = []
        #    for p in allperiods:
        #        if teacher in getTeachers(p.teachers):
        #            pids.append(p.pk)

        #    qset1 = Period.objects.filter(pk__in=pids)

        ## for student the class must match the routine's class
        #student = getStudent(self.request.user)
        #if student:
        #    qset2 = Period.objects.filter(routine__p_class__pk=student.group.p_class.pk)

        #return qset1 | qset2
