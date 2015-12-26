from django.db import models


class Post(models.Model):
    title = models.CharField(max_length=150, null=True, blank=True)
    body = models.TextField()
    posted_on = models.DateTimeField()
    posted_by = models.CharField(max_length=100)
    event_on = models.DateField(null=True, blank=True)


class Comment(models.Model):
    post = models.ForeignKey(Post)
    body = models.TextField()
    posted_on = models.DateTimeField()
    posted_by = models.CharField(max_length=100)


class Assignment(models.Model):
    title = models.CharField(max_length=150)
    body = models.TextField()
    subject = models.CharField(max_length=100)
    posted_on = models.DateTimeField()
    posted_by = models.CharField(max_length=100)
    submission_date = models.DateField(null=True, blank=True)


class Submission(models.Model):
    assignment = models.ForeignKey(Assignment)
    body = models.TextField()
    posted_on = models.DateTimeField()
    posted_by = models.CharField(max_length=100)
