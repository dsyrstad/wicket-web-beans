package net.sourceforge.wicketwebbeans.databinder;

import java.util.Set;
import java.util.LinkedHashSet;

import net.databinder.models.ICriteriaBuilder;

import org.hibernate.Criteria;

public class CriteriaBuilderDelegate implements ICriteriaBuilderDelegate
{
	private Set<ICriteriaBuilder> builders = new LinkedHashSet<ICriteriaBuilder>();
	
	public void addCriteriaBuilder(ICriteriaBuilder criteriaBuilder) {
		builders.add(criteriaBuilder);
	}

	public Iterable<ICriteriaBuilder> criteriaBuilders() {
		return builders;
	}

	public void removeCriteriaBuilder(ICriteriaBuilder criteriaBuilder) {
		builders.remove(criteriaBuilder);
	}

	public void build(Criteria criteria){
		for(ICriteriaBuilder builder: criteriaBuilders())
			builder.build(criteria);
	}
}
