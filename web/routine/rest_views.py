from rest_framework import viewsets
from rest_framework import permissions

from routine.models import *
from routine.serializers import *
from classroom.permissions import *


class RoutineViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Routine.objects.all()
    serializer_class = RoutineSerializer


class PeriodViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Period.objects.all()
    serializer_class = PeriodSerializer
