from django.db import models
from django.contrib.auth.models import User, UserManager


class Organization(models.Model):
    name = models.CharField(max_length=50)
    admins = models.ManyToManyField(User)

    def __str__(self):
        return self.name


class Department(models.Model):
    name = models.CharField(max_length=50)
    organization = models.ForeignKey(Organization)

    def __str__(self):
        return self.name + ", " + str(self.organization)


class Teacher(models.Model):
    user = models.ForeignKey(User, null=True, blank=True)
    username = models.CharField(max_length=50, null=True, blank=True)
    department = models.ForeignKey(Department, null=True, blank=True)

    class Meta:
        verbose_name = "teacher"

    def __str__(self):
        if self.user:
            return self.user.first_name + " " + self.user.last_name
        elif self.username:
            return self.username
        else:
            return "unnamed"


class Subject(models.Model):
    name = models.CharField(max_length=30)
    department = models.ForeignKey(Department, null=True, blank=True)

    def __str__(self):
        return self.name


class Class(models.Model):
    class_id = models.CharField(max_length=30)
    department = models.ForeignKey(Department, null=True, blank=True)
    admins = models.ManyToManyField(User)

    class Meta:
        verbose_name_plural = "classes"

    def __str__(self):
        return self.class_id


class Group(models.Model):
    group_id = models.CharField(max_length=10)
    p_class = models.ForeignKey(Class, verbose_name="class")

    def __str__(self):
        return self.p_class.class_id + "(" + self.group_id + ")"


class Student(models.Model):
    user = models.ForeignKey(User)
    group = models.ForeignKey(Group)

    class Meta:
        verbose_name = "student"

    def __str__(self):
        return self.user.username
