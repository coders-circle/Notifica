from rest_framework import serializers
from classroom.models import *


class OrganizationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Organization
        fields = ('id', 'name')


class DepartmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Department
        fields = ('id', 'name', 'organization')


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'first_name', 'last_name', 'username', 'email')


class TeacherSerializer(serializers.ModelSerializer):
    user = UserSerializer()

    class Meta:
        model = Teacher
        fields = ('id', 'user', 'department')


class SubjectSerializer(serializers.ModelSerializer):
    class Meta:
        model = Teacher
        fields = ('id', 'name', 'department')


class ClassSerializer(serializers.ModelSerializer):
    class Meta:
        model = Class
        fields = ('id', 'class_id', 'department')


class GroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = Group
        fields = ('id', 'group_id', 'p_class')


class StudentSerializer(serializers.ModelSerializer):
    user = UserSerializer()

    class Meta:
        model = Student
        fields = ('id', 'user', 'group')
