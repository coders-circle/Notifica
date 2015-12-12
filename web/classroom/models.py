from django.db import models
from django.contrib.auth.models import User, UserManager


class Organization(models.Model):
    name = models.CharField(max_length=50)

    def __str__(self):
        return self.name


class Department(models.Model):
    name = models.CharField(max_length=50)
    organization = models.ForeignKey(Organization)

    def __str__(self):
        return self.name + ", " + str(self.organization)


class Teacher(models.Model):
    user = models.OneToOneField(User)
    department = models.ForeignKey(Department)

    class Meta:
        verbose_name = "teacher"

    def __str__(self):
        return self.user.username


class Subject(models.Model):
    name = models.CharField(max_length=30)
    department = models.ForeignKey(Department)

    def __str__(self):
        return self.name


class Class(models.Model):
    class_id = models.CharField(max_length=30)
    department = models.ForeignKey(Department)

    def __str__(self):
        return self.class_id + ", " + str(self.department)


class Group(models.Model):
    group_id = models.CharField(max_length=10)
    p_class = models.ForeignKey(Class, verbose_name="class")

    def __str__(self):
        return self.p_class.class_id + "(" + self.group_id + "), " + str(self.p_class.department)


class Student(models.Model):
    user = models.OneToOneField(User)
    group = models.ForeignKey(Group)

    class Meta:
        verbose_name = "student"

    def __str__(self):
        return self.user.username
