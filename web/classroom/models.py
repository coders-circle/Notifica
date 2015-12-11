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


class Teacher(User):
    department = models.ForeignKey(Department)

    objects = UserManager()

    class Meta:
        verbose_name = "teacher"

    def __str__(self):
        return self.get_username();


class Subject(models.Model):
    name = models.CharField(max_length=30)
    department = models.ForeignKey(Department)

    def __str__(self):
        return name


class Class(models.Model):
    classId = models.CharField(max_length=30)
    department = models.ForeignKey(Department)

    def __str__(self):
        return self.classId + ", " + str(self.department)


class Group(models.Model):
    groupId = models.CharField(max_length=10)
    pClass = models.ForeignKey(Class)

    def __str__(self):
        return self.pClass.classId + "(" + self.groupId + "), " + str(self.pClass.department)


class Student(User):
    group = models.ForeignKey(Group)

    objects = UserManager()

    class Meta:
        verbose_name = "student"

    def __str__(self):
        return self.get_username()
