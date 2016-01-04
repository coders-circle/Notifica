from rest_framework import serializers
from routine.models import *


class RoutineSerializer(serializers.ModelSerializer):
    class Meta:
        model = Routine
        fields = ('id', 'p_class')


class PeriodSerializer(serializers.ModelSerializer):
    class Meta:
        model = Period
        fields = ('id', 'groups', 'routine', 'subject', 'teachers', 'period_type', 'start_time', 'end_time', 'day', 'remarks')
