from rest_framework import serializers
from posts.models import *
from classroom.serializers import UserSerializer


datetimeformat = "%Y-%m-%d-%H-%M-%S"

class PostSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)
    num_comments = serializers.SerializerMethodField()
    
    class Meta:
        model = Post
        fields = ('id', 'title', 'body', 'posted_at', 'modified_at', 'posted_by', 'tags', 'num_comments', 'profile')
        read_only_fields = ('posted_by', 'posted_at', 'modified_at')

    def get_num_comments(self, post):
        return Comment.objects.filter(post=post).count()


class CommentSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)

    class Meta:
        model = Comment
        fields = ('id', 'post', 'body', 'posted_at', 'modified_at', 'posted_by')
        read_only_fields = ('posted_by', 'posted_at', 'modified')


class EventSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)
    
    class Meta:
        model = Event
        fields = ('id', 'title', 'body', 'posted_at', 'modified_at', 'event_at', 'posted_by', 'tags')
        read_only_fields = ('posted_by', 'posted_at', 'modified_at')


class AssignmentSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)

    class Meta:
        model = Assignment
        fields = ('id', 'title', 'body', 'subject', 'posted_by', 'posted_at', 'modified', 'submission_date')
        read_only_fields = ('posted_by', 'posted_at', 'modified')


class SubmissionSerializer(serializers.ModelSerializer):
    posted_by = UserSerializer(read_only=True)

    class Meta:
        model = Submission
        fields = ('id', 'assignment', 'body', 'posted_by', 'posted_at', 'modified')
        read_only_fields = ('posted_by', 'posted_at', 'modified')
