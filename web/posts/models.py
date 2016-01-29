from django.db import models
from django.contrib.auth.models import User

from classroom.models import *


class Content(models.Model):
    body = models.TextField()
    posted_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now=True)
    posted_by = models.ForeignKey(User)
    links = models.TextField(blank=True, default="[]")        # json array of urls

    class Meta:
        abstract = True
        ordering = ('modified_at', 'posted_at',)
    

class Post(Content):
    title = models.CharField(max_length=150, null=True, blank=True)
    tags = models.TextField(blank=True, default="[]")
    profile = models.ForeignKey(Profile)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\""


class Comment(Content):
    post = models.ForeignKey(Post)

    def __str__(self):
        return self.body[:10]


class Event(Content):
    title = models.CharField(max_length=150)
    event_at = models.DateField()
    tags = models.TextField(blank=True)
    profile = models.ForeignKey(Profile)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\"" + str(self.event_at)


class Assignment(Content):
    title = models.CharField(max_length=150)
    subject = models.CharField(max_length=100)
    submission_date = models.DateField(null=True, blank=True)
    profile = models.ForeignKey(Profile)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\""


class Submission(Content):
    assignment = models.ForeignKey(Assignment)

    def __str__(self):
        return self.body[:10]
