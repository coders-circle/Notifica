from django.contrib import admin
from posts.models import *


class CommentInline(admin.StackedInline):
    model = Comment
    extra = 1


class PostAdmin(admin.ModelAdmin):
    inlines = [CommentInline]


class SubmissionInline(admin.StackedInline):
    model = Submission
    extra = 1


class AssignmentAdmin(admin.ModelAdmin):
    inlines = [SubmissionInline]


admin.site.register(Post, PostAdmin)
admin.site.register(Comment)
admin.site.register(Assignment, AssignmentAdmin)
admin.site.register(Submission)
