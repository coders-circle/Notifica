# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-02-26 14:04
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0002_gcmregistration'),
    ]

    operations = [
        migrations.AlterField(
            model_name='request',
            name='status',
            field=models.IntegerField(choices=[(0, 'Unaccepted'), (1, 'Accepted'), (2, 'Rejected'), (2, 'Acknowledged')], default=0),
        ),
    ]
