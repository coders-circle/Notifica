from rest_framework import serializers
from posts.models import *
from classroom.serializers import UserSerializer


class PostSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)
    class Meta:
        model = Post
        fields = ('id', 'title', 'body', 'posted_on', 'posted_by', 'event_on', 'tags')
        read_only_fields = ('posted_by', 'posted_on')


class CommentSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)
    class Meta:
        model = Comment
        fields = ('id', 'post', 'body', 'posted_on', 'posted_by')
        read_only_fields = ('posted_by', 'posted_on')


class AssignmentSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)
    class Meta:
        model = Assignment
        fields = ('id', 'title', 'body', 'subject', 'posted_by', 'posted_on', 'submission_date')
        read_only_fields = ('posted_by', 'posted_on')


class SubmissionSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)
    class Meta:
        model = Submission
        fields = ('id', 'assignment', 'body', 'posted_by', 'posted_on')
        read_only_fields = ('posted_by', 'posted_on')
