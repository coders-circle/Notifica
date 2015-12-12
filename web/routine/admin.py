from django.contrib import admin
from routine.models import *

class PeriodInline(admin.StackedInline):
    model = Period
    extra = 5

class RoutineAdmin(admin.ModelAdmin):
    inlines = [PeriodInline]



admin.site.register(Routine, RoutineAdmin)
admin.site.register(Period)
