select * from
REFERENCE_SOURCE ref
left join REFERENCE_SOURCE_ANNOTATION r2a on r2a.REFERENCE_SOURCE_ID = ref.id
left join ANNOTATION an on r2a.ANNOTATIONS_ID = an.id
left join ANNOTATION_TYPE_ANNOTATION a2at on an.id = a2at.ANNOTATIONS_ID
left join ANNOTATION_TYPE antype on a2at.ANNOTATION_TYPE_ID = antype.id
where ref.acc = 'ISATAB_METADATA'
