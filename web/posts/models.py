from django.db import models
from django.contrib.auth.models import User


class Post(models.Model):
    title = models.CharField(max_length=150, null=True, blank=True)
    body = models.TextField()
    posted_on = models.DateTimeField(auto_now_add=True)
    posted_by = models.ForeignKey(User)
    event_on = models.DateField(null=True, blank=True)
    tags = models.TextField(blank=True)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\""

    class Meta:
        ordering = ('posted_on',)


class Comment(models.Model):
    post = models.ForeignKey(Post)
    body = models.TextField()
    posted_on = models.DateTimeField(auto_now_add=True)
    posted_by = models.ForeignKey(User)

    def __str__(self):
        return self.body[:10]


class Assignment(models.Model):
    title = models.CharField(max_length=150)
    body = models.TextField()
    subject = models.CharField(max_length=100)
    posted_on = models.DateTimeField(auto_now_add=True)
    posted_by = models.ForeignKey(User)
    submission_date = models.DateField(null=True, blank=True)

    def __str__(self):
        return self.title + " \"" + self.body[:10] + "\""


class Submission(models.Model):
    assignment = models.ForeignKey(Assignment)
    body = models.TextField()
    posted_on = models.DateTimeField(auto_now_add=True)
    posted_by = models.ForeignKey(User)

    def __str__(self):
        return self.body[:10]
