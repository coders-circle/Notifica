from django.db import models
from django.contrib.auth.models import User

from classroom.models import *


class Post(models.Model):
    title = models.CharField(max_length=150, null=True, blank=True)
    body = models.TextField()
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)
    posted_by = models.ForeignKey(User)
    tags = models.TextField(blank=True)
    profile = models.ForeignKey(Profile)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\""

    class Meta:
        ordering = ('modified_at', 'posted_at',)


class Comment(models.Model):
    post = models.ForeignKey(Post)
    body = models.TextField()
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)
    posted_by = models.ForeignKey(User)

    def __str__(self):
        return self.body[:10]


class Event(models.Model):
    title = models.CharField(max_length=150)
    body = models.TextField()
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)
    event_at = models.DateField()
    posted_by = models.ForeignKey(User)
    tags = models.TextField(blank=True)
    profile = models.ForeignKey(Profile)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\"" + str(self.event_at)

    class Meta:
        ordering = ('modified_at', 'posted_at',)


class Assignment(models.Model):
    title = models.CharField(max_length=150)
    body = models.TextField()
    subject = models.CharField(max_length=100)
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)
    posted_by = models.ForeignKey(User)
    submission_date = models.DateField(null=True, blank=True)
    profile = models.ForeignKey(Profile)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\""


class Submission(models.Model):
    assignment = models.ForeignKey(Assignment)
    body = models.TextField()
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)
    posted_by = models.ForeignKey(User)

    def __str__(self):
        return self.body[:10]
