package venturus.org.br.ventuchat.util;

public enum Helper
{
    URI( "http://host:port" );

    private final String value;

    Helper( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
