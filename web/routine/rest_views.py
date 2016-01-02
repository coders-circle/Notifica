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
