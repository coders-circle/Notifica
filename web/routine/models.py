from django.db import models
from classroom.models import *


class Routine(models.Model):
    p_class = models.ForeignKey(Class)

    def __str__(self):
        return str(self.p_class.class_id)


DAYS = (
    (0, 'Sunday'),
    (1, 'Monday'),
    (2, 'Tuesday'),
    (3, 'Wednesday'),
    (4, 'Thursday'),
    (5, 'Friday'),
    (6, 'Saturday'),
)


class Period(models.Model):
    routine = models.ForeignKey(Routine)
    subject = models.ForeignKey(Subject)
    teachers = models.ManyToManyField(Teacher)
    start_time = models.IntegerField()
    end_time = models.IntegerField()
    day = models.IntegerField(choices=DAYS)
    remarks = models.TextField(blank=True)
    groups = models.ManyToManyField(Group, blank=True)

    def get_day_display(self):
        return DAYS[self.day][1]

    def __str__(self):
        return str(self.subject) + " " + self.get_day_display() + " " + str(self.start_time) + "-" + str(self.end_time) + " (" + str(self.routine.p_class) + ")"

    class Meta:
        ordering = ["day", "start_time"]
