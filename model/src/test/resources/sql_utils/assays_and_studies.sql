select ass.*, ep.OBJ_TYPE as EP_type, ep.ACC as EP_acc, tech.OBJ_TYPE as tech_type, tech.ACC as tech_acc, s.ACC study_acc from assay ass
left join study s on ass.STUDY_ID = s.id
left join ONTOLOGY_ENTRY ep on ep.ID = ass.ENDPOINT
left join ONTOLOGY_ENTRY tech on tech.ID = ass.TECHNOLOGY
