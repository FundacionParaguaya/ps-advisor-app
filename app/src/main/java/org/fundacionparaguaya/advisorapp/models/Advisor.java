package org.fundacionparaguaya.advisorapp.models;

import java.net.URL;
import java.util.List;

/**
 * An advisor is the main user. They are responsible for taking snapshots, writing notes,
 * and helping families overcome poverty.
 */

public class Advisor
{
    private URL mProfilePictureUrl;
    private String mName;
    private String mEmail;
    private List<Family> families;
    private Organization organization;
}
