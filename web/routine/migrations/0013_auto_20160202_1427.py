# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-02-02 14:27
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('routine', '0012_auto_20160111_1230'),
    ]

    operations = [
        # migrations.RemoveField(
        #     model_name='elective',
        #     name='period_ptr',
        # ),
        # migrations.RemoveField(
        #     model_name='elective',
        #     name='students',
        # ),
        migrations.DeleteModel(
            name='Elective',
        ),
    ]
