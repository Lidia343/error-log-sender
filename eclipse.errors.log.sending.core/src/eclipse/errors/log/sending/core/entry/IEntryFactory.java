package eclipse.errors.log.sending.core.entry;

import java.util.List;

/**
 * Интерфейс фабрики для получения нескольких
 * вложений Entry архива.
 */
public interface IEntryFactory 
{
	List<Entry> getEntries () throws Exception;
}
