# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-01-02 08:39
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('posts', '0003_auto_20160102_0834'),
    ]

    operations = [
        migrations.AlterField(
            model_name='assignment',
            name='posted_on',
            field=models.DateTimeField(auto_now_add=True),
        ),
        migrations.AlterField(
            model_name='comment',
            name='posted_on',
            field=models.DateTimeField(auto_now_add=True),
        ),
        migrations.AlterField(
            model_name='post',
            name='posted_on',
            field=models.DateTimeField(auto_now_add=True),
        ),
        migrations.AlterField(
            model_name='submission',
            name='posted_on',
            field=models.DateTimeField(auto_now_add=True),
        ),
    ]
