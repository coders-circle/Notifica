from django.db.models import Q
from django.utils import dateparse

from rest_framework import viewsets
from rest_framework import permissions

from classroom.utils import *
from posts.models import *
from posts.serializers import *

from main.search import *

import datetime

class PostViewSet(viewsets.ModelViewSet):
    serializer_class = PostSerializer
    perimission_classes = (permissions.IsAuthenticated,)

    def perform_create(self, serializer):
        serializer.save(posted_by=self.request.user)

    def get_queryset(self):

        # search by query if necessary
        searchstring = self.request.GET.get("q")
        if searchstring and searchstring != "":
            regexstring = get_regex(searchstring)
            queryset = Post.objects.filter(Q(title__iregex=regexstring) | Q(body__iregex=regexstring) | Q(tags__iregex=regexstring))
        else:
            queryset = Post.objects.all()

        profile = self.request.GET.get("profile")
        if profile:
            queryset = queryset.filter(profile__pk=profile)

        # Filter queryset for those posted for classes and departments user belongs to
        # and those posted by user
        myprofiles = get_profiles(self.request.user)
        queryset = queryset.filter(Q(profile__pk__in=myprofiles) | Q(posted_by__pk=self.request.user.pk))

        # recent query set for posts later than given time
        timequery = self.request.GET.get("time")
        recentQueryset = Post.objects.none()
        if timequery:
            time = dateparse.parse_datetime(timequery)
            recentQueryset = queryset.filter(modified_at__gt=time)

        # slice the result to desired offset:count

        offset = self.request.GET.get("offset")
        count = self.request.GET.get("count")

        if not count and timequery:
            if not offset:
                return recentQueryset
            else:
                return recentQueryset[int(offset):]

        if offset:
            queryset = queryset[int(offset):]
        if count:
            count = int(count) - recentQueryset.count()
            queryset = queryset[:count]

        time = self

        result = []
        for x in queryset:
            result.append(x.pk)

        for y in queryset:
            result.append(y.pk)

        return Post.objects.filter(pk__in=list(set(result)))


class CommentViewSet(viewsets.ModelViewSet):
    serializer_class = CommentSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(posted_by=self.request.user)

    def get_queryset(self):
        postid = self.request.GET.get("postid")
        if postid:
            return Comment.objects.filter(post__pk=postid)
        return Comment.objects.all()


class EventViewSet(viewsets.ModelViewSet):
    queryset = Event.objects.all()
    serializer_class = PostSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(posted_by=self.request.user)


class AssignmentViewSet(viewsets.ModelViewSet):
    queryset = Assignment.objects.all()
    serializer_class = AssignmentSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(posted_by=self.request.user)


class SubmissionViewSet(viewsets.ModelViewSet):
    queryset = Submission.objects.all()
    serializer_class = SubmissionSerializer
    perimission_classes = (permissions.IsAuthenticatedOrReadOnly,)

    def perform_create(self, serializer):
        serializer.save(posted_by=self.request.user)
