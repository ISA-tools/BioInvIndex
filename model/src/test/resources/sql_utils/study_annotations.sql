select s.id, s.title, at.VALUE, a.TEXT
from STUDY s
left join STUDY_ANNOTATION s2a ON s.id = s2a.STUDY_ID
left join ANNOTATION a ON s2a.ANNOTATIONS_ID = a.id
left join ANNOTATION_TYPE at ON a.TYPE_ID = at.ID
