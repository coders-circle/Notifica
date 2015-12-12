from django.db import models
from django.contrib.auth.models import User, UserManager


class Organization(models.Model):
    name = models.CharField(max_length=50)
    admins = models.ManyToManyField(User)
    notifica_id = models.CharField(max_length=32, unique=True)

    def __str__(self):
        return self.name + " @organization:" + self.notifica_id


class Department(models.Model):
    name = models.CharField(max_length=50)
    organization = models.ForeignKey(Organization)
    notifica_id = models.CharField(max_length=32, unique=True)

    def __str__(self):
        return self.name + ", " + str(self.organization) + " @department:" + self.notifica_id


class Teacher(models.Model):
    user = models.ForeignKey(User)
    department = models.ForeignKey(Department)
    notifica_id = models.CharField(max_length=32, unique=True)

    class Meta:
        verbose_name = "teacher"

    def __str__(self):
        return self.user.username + " @teacher:" + self.notifica_id


class Subject(models.Model):
    name = models.CharField(max_length=30)
    department = models.ForeignKey(Department)
    notifica_id = models.CharField(max_length=32, unique=True)

    def __str__(self):
        return self.name + " @subject:" + self.notifica_id


class Class(models.Model):
    class_id = models.CharField(max_length=30)
    department = models.CharField(max_length=100)
    admins = models.ManyToManyField(User)
    notifica_id = models.CharField(max_length=32, unique=True)

    class Meta:
        verbose_name_plural = "classes"

    def __str__(self):
        return self.class_id + " @class:" + self.notifica_id


class Group(models.Model):
    group_id = models.CharField(max_length=10)
    p_class = models.ForeignKey(Class, verbose_name="class")
    notifica_id = models.CharField(max_length=32, unique=True)

    def __str__(self):
        return self.p_class.class_id + "(" + self.group_id + ") @group:" + self.notifica_id


class Student(models.Model):
    user = models.ForeignKey(User)
    group = models.ForeignKey(Group)
    notifica_id = models.CharField(max_length=32, unique=True)

    class Meta:
        verbose_name = "student"

    def __str__(self):
        return self.user.username + " @student:" + self.notifica_id
