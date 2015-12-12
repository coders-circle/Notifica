from rest_framework import viewsets
from rest_framework import permissions

from classroom.models import *
from classroom.serializers import *
from classroom.permissions import *


class OrganizationViewSet(viewsets.ModelViewSet):
    queryset = Organization.objects.all()
    serializer_class = OrganizationSerializer
    perimission_classes = (IsAdminOrReadOnly,)


class DepartmentViewSet(viewsets.ModelViewSet):
    queryset = Department.objects.all()
    serializer_class = DepartmentSerializer
    permission_classes = (IsAdminOrReadOnly,)


class TeacherViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Teacher.objects.all()
    serializer_class = TeacherSerializer


class SubjectViewSet(viewsets.ModelViewSet):
    queryset = Subject.objects.all()
    serializer_class = SubjectSerializer
    permission_classes = (IsAdminOrReadOnly,)


class ClassViewSet(viewsets.ModelViewSet):
    queryset = Class.objects.all()
    serializer_class = ClassSerializer
    permission_classes = (IsAdminOrReadOnly,)


class GroupViewSet(viewsets.ModelViewSet):
    queryset = Group.objects.all()
    serializer_class = GroupSerializer
    permission_classes = (IsAdminOrReadOnly,)


class StudentViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Student.objects.all()
    serializer_class = StudentSerializer
    permission_classes = (IsAdminOrReadOnly,)
