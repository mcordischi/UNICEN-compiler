package event;

import symbolTable.*;



public interface Event
{

    public abstract void eventNewToken(TableEntry token);

    public abstract void eventWarning(String s);

    public abstract void eventError(String s, int i, String origin);

    public abstract void eventNewEntry(SymbolTable st, TableEntry newEntry);
    
    public abstract void eventNewRule(String rule, int line);
    
    public abstract void eventNewPolishItem(String item);
}