from django.db.models import Q

from rest_framework import viewsets, permissions
from rest_framework.views import APIView
from rest_framework.response import Response

from classroom.models import *
from classroom.utils import *
from classroom.serializers import *
from classroom.permissions import *
from routine.models import *

from main.search import *


class ProfileViewSet(viewsets.ModelViewSet):
    queryset = Profile.objects.all()
    serializer_class = ProfileSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly,)


class OrganizationViewSet(viewsets.ModelViewSet):
    serializer_class = OrganizationSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Organization.objects.filter(name__iregex=regexstring)
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
            queryset = Department.objects.filter(name__iregex=regexstring)
        else:
            queryset = Department.objects.all()

        # get departments that user belongs to
        user = self.request.GET.get("user")
        if user:
            queryset = queryset.filter(pk__in=get_departments(User.objects.get(pk=user)))

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
            queryset = Teacher.objects.filter(Q(user__first_name__iregex=regexstring) | Q(user__last_name__iregex=regexstring) | Q(user__username__iregex=regexstring) | Q(username__iregex=regexstring))
        else:
            queryset = Teacher.objects.all()

        p_class = self.request.GET.get("class")
        if p_class:
            periods = Period.objects.filter(routine__p_class__pk=p_class)
            teachers = periods.values_list("teachers", flat=True)
            queryset = queryset.filter(pk__in=teachers)

        user = self.request.GET.get("user")
        if user:
            queryset = queryset.filter(user__pk=user)

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
            queryset = Subject.objects.filter(name__iregex=regexstring)
        else:
            queryset = Subject.objects.all()

        return queryset


class ElectiveViewSet(viewsets.ModelViewSet):
    serializer_class = ElectiveSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        p_class = serializer.validated_data['p_class']
        if self.request.user not in p_class.admins.all():
            raise serializers.ValidationError("You have not permission to add elective to this class")
        serializer.save()

    def get_queryset(self):
        subject = self.request.GET.get('subject')
        if subject:
            queryset = Elective.objects.filter(subject__pk=subject)
        else:
            queryset = Elective.objects.all()

        p_class = self.request.GET.get('class')
        if p_class:
            queryset = queryset.filter(p_class__pk=p_class)

        return queryset


class ClassViewSet(viewsets.ModelViewSet):
    serializer_class = ClassSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Class.objects.filter(class_id__iregex=regexstring)
        else:
            queryset = Class.objects.all()

        # get classes that a user belongs to
        user = self.request.GET.get("user")
        if user:
            queryset = queryset.filter(pk__in=get_classes(User.objects.get(pk=user)))

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
            queryset = Group.objects.filter(group_id__iregex=regexstring)
        else:
            queryset = Group.objects.all()

        return queryset


class StudentViewSet(viewsets.ModelViewSet):
    serializer_class = StudentSerializer
    permission_classes = (permissions.IsAuthenticatedOrReadOnly, IsAdminOrReadOnly,)

    def perform_create(self, serializer):
        group = serializer.validated_data["group"]
        if self.request.user not in group.p_class.admins.all():
            raise serializers.ValidationError("You have not permission to add student to this class")
        serializer.save()

    def get_queryset(self):
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Student.objects.filter(Q(user__first_name__iregex=regexstring) | Q(user__last_name__iregex=regexstring) | Q(user__username__iregex=regexstring))
        else:
            queryset = Student.objects.all()

        p_class = self.request.GET.get("class")
        if p_class:
            queryset = queryset.filter(group__p_class__pk=p_class)

        user = self.request.GET.get("user")
        if user:
            queryset = queryset.filter(user__pk=user)

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

        # filter by username as well
        username = self.request.GET.get("username")
        if username:
            queryset = queryset.filter(username=username)

        return queryset
