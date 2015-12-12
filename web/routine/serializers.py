from rest_framework import serializers
from routine.models import *


class RoutineSerializer(serializers.ModelSerializer):
    class Meta:
        model = Routine
        fields = ('id', 'notifica_id', 'p_class')


class PeriodSerializer(serializers.ModelSerializer):
    class Meta:
        model = Period
        fields = ('id', 'notifica_id', 'groups', 'routine', 'subject', 'teachers', 'start_time', 'end_time', 'day', 'remarks')
