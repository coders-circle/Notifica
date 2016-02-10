from rest_framework import serializers
from routine.models import *


class RoutineSerializer(serializers.ModelSerializer):
    class Meta:
        model = Routine
        fields = ('id', 'p_class', 'created_at', 'modified_at')


class PeriodSerializer(serializers.ModelSerializer):
    class Meta:
        model = Period
        fields = ('id', 'groups', 'routine', 'subject', 'teachers', 'period_type', 'start_time', 'end_time', 'day', 'remarks')


class ElectiveSerializer(serializers.ModelSerializer):
    group = serializers.SerializerMethodField()
    routine = serializers.SerializerMethodField()

    class Meta:
        model = Elective
        fields = ('id', 'group', 'routine', 'subject', 'teachers', 'period_type', 'start_time', 'end_time', 'day', 'remarks', 'students')

    def get_group(self, elective):
        return elective.p_group.name

    def get_routine(self, elective):
        return elective.p_group.routine.pk
