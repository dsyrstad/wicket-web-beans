package wicket.contrib.webbeans.databinder;

import net.databinder.models.ICriteriaBuilder;

public interface ICriteriaBuilderDelegate extends ICriteriaBuilder
{
	public void addCriteriaBuilder(ICriteriaBuilder criteriaBuilder);
	public void removeCriteriaBuilder(ICriteriaBuilder criteriaBuilder);
	public Iterable<ICriteriaBuilder> criteriaBuilders();
}
