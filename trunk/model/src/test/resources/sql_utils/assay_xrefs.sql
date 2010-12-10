select * from ASSAY ass
left join ASSAY_ANNOTATION a2a on ass.ID = a2a.ASSAY_ID
left join xref on a2a.ANNOTATIONS_ID = xref.ID
left join REFERENCE_SOURCE src on xref.SOURCE_ID = src.ID