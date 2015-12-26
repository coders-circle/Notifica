from rest_framework import serializers
from posts.models import *


class PostSerializer(serializers.ModelSerializer):
    class Meta:
        model = Post
        fields = ('id', 'title', 'body', 'posted_on', 'posted_by', 'event_on')
        read_only_fields = ('posted_by',)


class CommentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Comment
        fields = ('id', 'post', 'body', 'posted_on', 'posted_by')
        read_only_fields = ('posted_by',)


class AssignmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Assignment
        fields = ('id', 'title', 'body', 'subject', 'posted_by', 'posted_on', 'submission_date')
        read_only_fields = ('posted_by',)


class SubmissionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Submission
        fields = ('id', 'assignment', 'body', 'posted_by', 'posted_on')
        read_only_fields = ('posted_by',)
