package seng202.team3.unittests.models.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.models.annotation.Label;
import seng202.team3.models.annotation.LabelUtils;

public class AnnotatedClassTest {
  TestModel testModel;

  class TestModel {
    @Label(value = "Id")
    public Integer id;

    @Label(value = "Name")
    public String name;

    @Label(value = "Way more complicated")
    public String wayMoreComplicated;

    public String unlabled;
  }

  @BeforeEach
  void beforeEach() {
    testModel = new TestModel();
  }

  @Test
  void ListLabledAttributes() {
    List<String> labledAttributes = LabelUtils.getLabeledAttributes(TestModel.class);

    assertEquals(3, labledAttributes.size());

    assertEquals(labledAttributes, Arrays.asList("id", "name", "wayMoreComplicated"));
  }

  @Test
  void GetLabel() throws NoSuchFieldException {
    String idLabel = LabelUtils.getLabel(TestModel.class, "id");
    assertEquals(idLabel, "Id");

    String nameLabel = LabelUtils.getLabel(TestModel.class, "name");
    assertEquals(nameLabel, "Name");

    String wayMoreComplicatedLabel = LabelUtils.getLabel(TestModel.class, "wayMoreComplicated");
    assertEquals(wayMoreComplicatedLabel, "Way more complicated");
  }

  @Test
  void GetUnlabledLabel() throws NoSuchFieldException {
    assertEquals(LabelUtils.getLabel(TestModel.class, "unlabled"), null);

    assertThrows(
        NoSuchFieldException.class, () -> LabelUtils.getLabel(TestModel.class, "noExistant"));
  }
}
