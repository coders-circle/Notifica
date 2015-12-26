from django.db import models
from classroom import models as classroom_models


class Routine(models.Model):
    p_class = models.ForeignKey(classroom_models.Class)
    notifica_id = models.CharField(max_length=32, unique=True)

    def __str__(self):
        return str(self.p_class.class_id) + " routine @routine:" + self.notifica_id


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
    subject = models.CharField(max_length=100)
    teachers = models.TextField()
    start_time = models.IntegerField()
    end_time = models.IntegerField()
    day = models.IntegerField(choices=DAYS)
    remarks = models.TextField()
    groups = models.ManyToManyField(classroom_models.Group, blank=True)

    def __str__(self):
        return str(self.subject) + " " + self.get_day_display() + " " + str(self.start_time) + "-" + str(self.end_time) + " (" + str(self.routine.p_class) + ")"

    class Meta:
        ordering = ["day", "start_time"]
