from django.db.models import Q

from rest_framework import viewsets
from rest_framework import permissions

from classroom.models import *
from classroom.serializers import *
from classroom.permissions import *

from main.search import *


class OrganizationViewSet(viewsets.ModelViewSet):
    serializer_class = OrganizationSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(admins=[self.request.user])

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Organization.objects.filter(Q(notifica_id__iregex=regexstring) | Q(name=regexstring))
        else:
            queryset = Organization.objects.all()

        return queryset


class DepartmentViewSet(viewsets.ModelViewSet):
    serializer_class = DepartmentSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        organization = serializer.validated_data["organization"]
        if self.request.user not in organization.admins.all():
            raise serializers.ValidationError("You have not permission to add department to this organization")
        serializer.save()

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Department.objects.filter(Q(notifica_id__iregex=regexstring) | Q(name=regexstring))
        else:
            queryset = Department.objects.all()

        return queryset


class TeacherViewSet(viewsets.ModelViewSet):
    serializer_class = TeacherSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        department = serializer.validated_data["department"]
        if self.request.user not in department.organization.admins.all():
            raise serializers.ValidationError("You have not permission to add teacher to this department")
        serializer.save()

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Teacher.objects.filter(Q(user__first_name__iregex=regexstring) | Q(user__last_name__iregex=regexstring) | Q(user__username__iregex=regexstring) | Q(notifica_id__iregex=regexstring))
        else:
            queryset = Teacher.objects.all()

        return queryset


class SubjectViewSet(viewsets.ModelViewSet):
    serializer_class = SubjectSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        department = serializer.validated_data["department"]
        if self.request.user not in department.organization.admins.all():
            raise serializers.ValidationError("You have not permission to add subject to this department")
        serializer.save()

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Subject.objects.filter(Q(notifica_id__iregex=regexstring) | Q(name=regexstring))
        else:
            queryset = Subject.objects.all()

        return queryset


class ClassViewSet(viewsets.ModelViewSet):
    serializer_class = ClassSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(admins=[self.request.user])

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Class.objects.filter(Q(notifica_id__iregex=regexstring) | Q(class_id__iregex=regexstring))
        else:
            queryset = Class.objects.all()

        return queryset


class GroupViewSet(viewsets.ModelViewSet):
    serializer_class = GroupSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly)

    def perform_create(self, serializer):
        pclass = serializer.validated_data["p_class"]
        if self.request.user not in pclass.admins.all():
            raise serializers.ValidationError("You have not permission to add group to this class")
        serializer.save()

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Group.objects.filter(Q(notifica_id__iregex=regexstring) | Q(group_id__iregex=regexstring))
        else:
            queryset = Group.objects.all()

        return queryset


class StudentViewSet(viewsets.ModelViewSet):
    serializer_class = StudentSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        group = serializer.validated_data["group"]
        if self.request.user not in group.p_class.admins.all():
            raise serializers.ValidationError("You have not permission to add group to this class")
        serializer.save()

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Student.objects.filter(Q(user__first_name__iregex=regexstring) | Q(user__last_name__iregex=regexstring) | Q(user__username__iregex=regexstring) | Q(notifica_id__iregex=regexstring))
        else:
            queryset = Student.objects.all()

        return queryset


class UserViewSet(viewsets.ModelViewSet):
    serializer_class = UserSerializer
    permission_classes = (IsAdminOrReadOnly,)

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = User.objects.filter(Q(first_name__iregex=regexstring) | Q(last_name__iregex=regexstring) | Q(username__iregex=regexstring))
        else:
            queryset = User.objects.all()

        return queryset
