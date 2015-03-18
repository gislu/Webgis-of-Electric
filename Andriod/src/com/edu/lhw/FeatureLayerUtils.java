
package com.cityelc.ahu.lhw;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.esri.core.map.FeatureType;
import com.esri.core.map.Field;
import com.esri.core.map.Graphic;

public class FeatureLayerUtils {

  public enum FieldType {
    NUMBER, STRING, DECIMAL, DATE;

    public static FieldType determineFieldType(Field field) {

      if (field.getFieldType().equals("esriFieldTypeString")) {
        return FieldType.STRING;
      } else if (field.getFieldType().equals("esriFieldTypeSmallInteger")
          || field.getFieldType().equals("esriFieldTypeInteger")) {
        return FieldType.NUMBER;
      } else if (field.getFieldType().equals("esriFieldTypeSingle")
          || field.getFieldType().equals("esriFieldTypeDouble")) {
        return FieldType.DECIMAL;
      } else if (field.getFieldType().equals("esriFieldTypeDate")) {
        return FieldType.DATE;
      }
      return null;
    }
  }

  /**
   * Helper method to determine if a field should be shown in the list for editing
   */
  public static boolean isFieldValidForEditing(Field field) {

    String fieldType = field.getFieldType();

    if (field.isEditable() && !fieldType.equals("esriFieldTypeOID") && !fieldType.equals("esriFieldTypeGeometry")
        && !fieldType.equals("esriFieldTypeBlob") && !fieldType.equals("esriFieldTypeRaster")
        && !fieldType.equals("esriFieldTypeGUID") && !fieldType.equals("esriFieldTypeXML")) {

      return true;

    }

    return false;
  }

  /**
   * Helper method to set attributes on a graphic. Only sets the attributes on the newGraphic variable if the value has
   * changed. Returns true if the value has changed and has been set on the graphic.
   * 
   * @return boolean hasValueChanged
   */
  public static boolean setAttribute(Graphic newGraphic, Graphic oldGraphic, Field field, String value,
      DateFormat formatter) {

    boolean hasValueChanged = false;
      
    // check if the value is null and thats its changed, if so its been deleted
    // and can be saved as
    // such
    // if (value == null && value != oldGraphic.getAttributeValue(field.getName())) {

    // newGraphic.setAttributeValue(field.getName(), null);

    // } else if (value != null) {

    // if its a string, and it has changed from the oldGraphic value
    if (FieldType.determineFieldType(field) == FieldType.STRING) {
        
      if (!value.equals(oldGraphic.getAttributeValue(field.getName()))) {
        
        // set the value as it is
        newGraphic.setAttributeValue(field.getName(), value);
        hasValueChanged = true;
 
      }
    } else if (FieldType.determineFieldType(field) == FieldType.NUMBER) {

      // if its an empty string, its a 0 number value (nulls not supported), check this is a
      // change before making it a 0
      if ( value.equals("") && oldGraphic.getAttributeValue(field.getName()) != Integer.valueOf(0))  {

        // set a null value on the new graphic
        newGraphic.setAttributeValue(field.getName(), new Integer(0));
        hasValueChanged = true;

      } else {

        // parse as an int and check this is a change
        int intValue = Integer.parseInt(value);
        if (intValue != Integer.parseInt(oldGraphic.getAttributeValue(field.getName()).toString())) {

          newGraphic.setAttributeValue(field.getName(), Integer.valueOf(intValue));
          hasValueChanged = true;

        }
      }
    } else if (FieldType.determineFieldType(field) == FieldType.DECIMAL) {

      // if its an empty string, its a 0 double value (nulls not supported), check this is a
      // change before making it a 0
      if ( (value.equals("") && oldGraphic.getAttributeValue(field.getName()) != Double.valueOf(0))) {

        // set a null value on the new graphic
        newGraphic.setAttributeValue(field.getName(), new Double(0));
        hasValueChanged = true;

      } else {

        // parse as an double and check this is a change
        double dValue = Double.parseDouble(value);
        if (dValue != Double.parseDouble(oldGraphic.getAttributeValue(field.getName()).toString())) {

          newGraphic.setAttributeValue(field.getName(), Double.valueOf(dValue));
          hasValueChanged = true;

        }
      }
    } else if (FieldType.determineFieldType(field) == FieldType.DATE) {

      // if its a date, get the milliseconds value
      Calendar c = Calendar.getInstance();
      long dateInMillis = 0;

      try {

        // parse to a double and check this is a change
        c.setTime(formatter.parse(value));
        dateInMillis = c.getTimeInMillis();

        if (dateInMillis != Long.parseLong(oldGraphic.getAttributeValue(field.getName()).toString())) {

          newGraphic.setAttributeValue(field.getName(), Long.valueOf(dateInMillis));
          hasValueChanged = true;
        }
      } catch (ParseException e) {
        // do nothing
      }
    }
    // }

    return hasValueChanged;

  }

  /**
   * Helper method to find the Types actual value from its name
   */
  public static String returnTypeIdFromTypeName(FeatureType[] types, String name) {

    for (FeatureType type : types) {
      if (type.getName().equals(name)) {
        return type.getId();
      }
    }
    return null;
  }

  /**
   * Helper method to setup the editable field indexes and store them for later retrieval in getItem() method.
   */
  public static int[] createArrayOfFieldIndexes(Field[] fields) {

    // process count of fields and which are available for editing
    ArrayList<Integer> list = new ArrayList<Integer>();
    int fieldCount = 0;

    for (int i = 0; i < fields.length; i++) {

      if (isFieldValidForEditing(fields[i])) {

        list.add(Integer.valueOf(i));
        fieldCount++;

      }
    }

    int[] editableFieldIndexes = new int[fieldCount];

    for (int x = 0; x < list.size(); x++) {

      editableFieldIndexes[x] = list.get(x).intValue();

    }

    return editableFieldIndexes;
  }

  /**
   * Helper method to create a String array of Type values for populating a spinner
   */
  public static String[] createTypeNameArray(FeatureType[] types) {

    String[] typeNames = new String[types.length];
    int i = 0;
    for (FeatureType type : types) {

      typeNames[i] = type.getName();
      i++;

    }

    return typeNames;

  }

  /**
   * Helper method to create a HashMap of types using the Id (value) as the key
   */
  public static HashMap<String, FeatureType> createTypeMapByValue(FeatureType[] types) {

    HashMap<String, FeatureType> typeMap = new HashMap<String, FeatureType>();

    for (FeatureType type : types) {

      typeMap.put(type.getId(), type);

    }

    return typeMap;

  }

}
